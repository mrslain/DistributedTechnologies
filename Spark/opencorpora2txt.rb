#!/usr/bin/env ruby
# encoding: utf-8

# The MIT License (MIT)
#
# Copyright (c) 2013-2014 Dmitry Ustalov
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

require 'rubygems'
require 'nokogiri'
require 'csv'

Dir.mkdir 'opencorpora' unless File.directory? 'opencorpora'

buf, flag = '', false
parents, children, names = [], {}, {}

File.open('fileNames.txt', 'w') do |output|
File.foreach('annot.opcorpora.xml') do |s|
  s.tap(&:chomp!).tap(&:strip!)

  next unless flag ||= s =~ /<text.*>/
  buf << s

  unless flag &&= s !~ /<\/text.*>/
    doc = Nokogiri::XML(buf)

    id = doc.xpath('//text/@id').text.to_i
    parent = doc.xpath('//text/@parent').text.to_i
    paragraphs = doc.xpath('//text/paragraphs/paragraph')
    names[id] = doc.xpath('//text/@name').text

    if parent.zero?
      parents << id
    else
      children[id] = parent
    end

    unless paragraphs.empty?
      fileName = 'opencorpora/%04d.txt' % id
      File.open(fileName, 'w') do |f|
        paragraphs.each do |paragraph|
          f.puts paragraph.xpath('sentence/source').map(&:text).join(' ')
        end
      end
      output.puts fileName
    end

    puts 'text #%04d is done' % id
    buf.clear
  end
end
end
